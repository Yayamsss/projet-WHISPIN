JC := javac

SRC_DIR := src
CLS_DIR := class
JAVAFX_MODULES := javafx.base,javafx.controls,javafx.graphics

SRC := $(shell find $(SRC_DIR) -type f -name *.java)

Game:
	@echo Compiling...
	@mkdir -p $(CLS_DIR)
	@$(JC) $(SRC) --module-path $(PATH_TO_FX) --add-modules $(JAVAFX_MODULES) -d $(CLS_DIR)
	@echo Finished

Run: Game
	@echo Running...
	@java --module-path $(PATH_TO_FX) --add-modules $(JAVAFX_MODULES) --enable-native-access=javafx.graphics -cp class Program
