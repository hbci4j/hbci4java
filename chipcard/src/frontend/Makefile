JAVA_HOME = $(shell readlink -f /usr/bin/javac | sed "s:/bin/javac::")
DEFINES   = -D__UNIX
INCLUDES  = -I../include -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
WARNINGS  = -Wall
# removed "-Werror -pedantic" because of dlsym() typecast problem

CC       = g++
CFLAGS   = -fPIC $(DEFINES) $(INCLUDES) $(WARNINGS)
LIBS     = -L../../bin -lzkachip-highlevel -lzkachip-lowlevel

SOURCES = frontend.cpp

OBJECTS = ../../bin/frontend.o

.SUFFIXES: .cpp .o

../../bin/%.o: %.cpp
	$(CC) -c -o $@ $(CFLAGS) $<

all: depend ../../bin/libhbci4java-card-linux.so

depend: Makefile.depend

Makefile.depend: $(SOURCES)
	for file in $(SOURCES); do echo -n "../../bin/"; gcc $(CFLAGS) -MM $$file; done >Makefile.depend

../../bin/libhbci4java-card-linux.so: $(OBJECTS) ../../bin/libzkachip-highlevel.a ../../bin/libzkachip-lowlevel.a
	$(CC) -fPIC -shared -o $@ $(OBJECTS) $(LIBS) 

include Makefile.depend
