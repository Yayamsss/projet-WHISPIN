JAVAC := javac
JAVA := java
LIB_CP ?= lib/*

# JavaFX doit etre installe sur la machine.
# On detecte d'abord un chemin systeme connu.
JAVAFX_CANDIDATES := /usr/share/openjfx/lib /snap/openjfx/current/sdk/lib
JAVAFX_SYSTEM := $(firstword $(foreach p,$(JAVAFX_CANDIDATES),$(if $(wildcard $(p)/libprism_sw.so),$(p),)))
ifeq ($(strip $(JAVAFX_SYSTEM)),)
JAVAFX_SYSTEM := $(firstword $(wildcard $(JAVAFX_CANDIDATES)))
endif
JAVAFX_LIB ?= $(JAVAFX_SYSTEM)
JAVAFX_MODULES ?= javafx.controls,javafx.fxml
# Au lancement graphique, on utilise le meme module-path JavaFX.
JAVAFX_RUN_LIB ?= $(JAVAFX_LIB)
JAVA_RUN_OPTS ?= --enable-native-access=javafx.graphics

SRC := $(shell find . -name "*.java" \
	-not -path "./bin/*")
BIN := bin
MAIN := InterfacePrincipale

.PHONY: all build run test clean jar help

all: build

build:
	mkdir -p $(BIN)
	$(JAVAC) --module-path $(JAVAFX_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(LIB_CP)" -d $(BIN) $(SRC)

run: build
	$(JAVA) $(JAVA_RUN_OPTS) --module-path $(JAVAFX_RUN_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(BIN):$(LIB_CP)" $(MAIN)

test: build
	$(JAVA) --module-path $(JAVAFX_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(BIN):$(LIB_CP)" TestsMoteur
	$(JAVA) --module-path $(JAVAFX_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(BIN):$(LIB_CP)" TestsPersistance
	$(JAVA) --module-path $(JAVAFX_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(BIN):$(LIB_CP)" TestsNiveaux

jar: build
	jar --create --file sokoban.jar -C $(BIN) .

clean:
	rm -rf $(BIN) *.jar

help:
	@echo "Usage: make [target]"
	@echo "Targets:"
	@echo "  make         (alias for make build)"
	@echo "  make build   Compile all .java into $(BIN)" 
	@echo "  make run     Build and run the default main ($(MAIN)) with JavaFX options" 
	@echo "  make test    Build then run TestsMoteur, TestsPersistance and TestsNiveaux" 
	@echo "  make jar     Create sokoban.jar from compiled classes" 
	@echo "  make clean   Remove compiled classes and jars" 
	@echo "Environment: set JAVAFX_LIB (and optionally JAVAFX_RUN_LIB) if needed"

