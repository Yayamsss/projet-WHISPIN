# Makefile for Bibou Adventures
# Builds runnable jar and provides 'make run' to launch

JAVAC = javac
JAR = jar

SRC_DIR = src
OUT_DIR = out
RES_DIR = ./
JAR_FILE = BibouAdventures.jar

# Your main class with package
MAIN_CLASS = src.main.Main

# All Java source files
JAVA_FILES := $(shell find $(SRC_DIR) -name "*.java")

# Default target: build jar
all: $(JAR_FILE)

# Compile and create runnable jar
$(JAR_FILE): $(JAVA_FILES)
	@echo "Compiling Java files..."
	@mkdir -p $(OUT_DIR)
	$(JAVAC) -d $(OUT_DIR) $(JAVA_FILES)
	@echo "Creating runnable jar..."
	$(JAR) cfe $(JAR_FILE) $(MAIN_CLASS) -C $(OUT_DIR) . -C $(RES_DIR) .
	@echo "Done! Run with: java -jar $(JAR_FILE) or make run"

# Run the game directly
run: $(JAR_FILE)
	@echo "Running Bibou Adventures..."
	java -jar $(JAR_FILE)

# Clean build
clean:
	rm -rf $(OUT_DIR) $(JAR_FILE)
