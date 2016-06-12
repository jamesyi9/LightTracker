### Top Level Makefile ###

BIN = ./bin/
LIB = ./lib/*
SRC = ./src/
ODIR = -d $(BIN)
OS = $(shell uname)

# Determine 'CP' based on OS #

ifeq '$(OS)' 'Windows_NT'
	# For Windows
	CP = -cp '$(BIN);$(LIB)'
else ifeq '$(OS)' 'Darwin'
	# For OS X
	CP = -cp '$(BIN):$(LIB)'
else ifeq '$(OS)' 'Linux'
	# For Linux
	CP = -cp '$(BIN):$(LIB)'
else
	echo Unknown OS. Add condition for your OS. Use \'shell uname\' to determine your OS.
endif

### Make Rules ###

# General #

all:
	javac $(CP) $(ODIR) $(SRC)*.java

run:
	java $(CP) GUI

clean:
	rm -rf $(BIN)*.class
