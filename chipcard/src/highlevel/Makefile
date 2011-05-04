
DEFINES  = -D__UNIX
INCLUDES = -I../include
WARNINGS = -Wall
# removed "-Werror -pedantic" because of dlsym() typecast problem

CC       = g++
AR       = ar
CFLAGS   = -fPIC $(DEFINES) $(INCLUDES) $(WARNINGS)

SOURCES = hbci_cardtype.cpp \
          ddv_decrypt.cpp \
          ddv_encrypt.cpp \
          ddv_readbankdata.cpp \
          ddv_readkeydata.cpp \
     	  ddv_readsigid.cpp \
	      ddv_signdata.cpp \
	      ddv_pins.cpp \
	      ddv_writebankdata.cpp \
	      ddv_writesigid.cpp \
	      rsa_pins.cpp \
          rsa_cardnumber.cpp

OBJECTS = ../../bin/hbci_cardtype.o \
          ../../bin/ddv_decrypt.o \
          ../../bin/ddv_encrypt.o \
          ../../bin/ddv_readbankdata.o \
	      ../../bin/ddv_readkeydata.o \
	      ../../bin/ddv_readsigid.o \
	      ../../bin/ddv_signdata.o \
	      ../../bin/ddv_pins.o \
	      ../../bin/ddv_writebankdata.o \
	      ../../bin/ddv_writesigid.o \
	      ../../bin/rsa_pins.o \
          ../../bin/rsa_cardnumber.o

.SUFFIXES: .cpp .o

../../bin/%.o: %.cpp
	$(CC) -c -o $@ $(CFLAGS) $<

all: depend ../../bin/libzkachip-highlevel.a

depend: Makefile.depend

Makefile.depend: $(SOURCES)
	for file in $(SOURCES); do echo -n "../../bin/"; gcc $(CFLAGS) -MM $$file; done >Makefile.depend

../../bin/libzkachip-highlevel.a: $(OBJECTS)
	$(AR) rcsv $@ $(OBJECTS)

include Makefile.depend
