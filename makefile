JAVAC := javac
JAVA := java
LIB_CP ?= lib/*

# Adjust this if your JavaFX libs are elsewhere
# Prefer the system JavaFX install when available; override JAVAFX_LIB if needed.
JAVAFX_LIB ?= $(firstword $(wildcard /usr/share/openjfx/lib /snap/openjfx/current/sdk/lib))
JAVAFX_MODULES ?= javafx.controls,javafx.fxml

SRC := $(shell find . -name "*.java")
BIN := bin
MAIN := interface_main

.PHONY: all build run clean jar help

all: build

build:
	mkdir -p $(BIN)
	$(JAVAC) --module-path $(JAVAFX_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(LIB_CP)" -d $(BIN) $(SRC)

run: build
	$(JAVA) --module-path $(JAVAFX_LIB) --add-modules $(JAVAFX_MODULES) -cp "$(BIN):$(LIB_CP)" $(MAIN)

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
	@echo "  make jar     Create sokoban.jar from compiled classes" 
	@echo "  make clean   Remove compiled classes and jars" 
	@echo "Environment: set JAVAFX_LIB to your JavaFX library path if different"

