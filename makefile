JAVAC := javac
JAVA := java
LIB_CP ?= lib/*

# Prefer JavaFX jars vendored in ./lib when available, else fallback to system install.
JAVAFX_LOCAL_MARKER := $(firstword $(wildcard lib/javafx.controls.jar lib/javafx-controls*.jar))
JAVAFX_CANDIDATES := /usr/share/openjfx/lib /snap/openjfx/current/sdk/lib
JAVAFX_SYSTEM := $(firstword $(foreach p,$(JAVAFX_CANDIDATES),$(if $(wildcard $(p)/libprism_sw.so),$(p),)))
ifeq ($(strip $(JAVAFX_SYSTEM)),)
JAVAFX_SYSTEM := $(firstword $(wildcard $(JAVAFX_CANDIDATES)))
endif
ifdef JAVAFX_LIB
# Keep user-provided JAVAFX_LIB as-is.
else
ifeq ($(strip $(JAVAFX_LOCAL_MARKER)),)
JAVAFX_LIB := $(JAVAFX_SYSTEM)
else
JAVAFX_LIB := lib
endif
endif
JAVAFX_MODULES ?= javafx.controls,javafx.fxml
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
	$(JAVA) $(JAVA_RUN_OPTS) --module-path $(JAVAFX_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(BIN):$(LIB_CP)" $(MAIN)

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
	@echo "Environment: set JAVAFX_LIB to your JavaFX library path if different"

