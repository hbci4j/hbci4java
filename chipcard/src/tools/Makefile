
DEFINES  = -D__UNIX -g
INCLUDES = -I../include
WARNINGS = -Wall
# removed "-Werror -pedantic" because of dlsym() typecast problem

CC       = g++
CFLAGS   = -fPIC $(DEFINES) $(INCLUDES) $(WARNINGS)
LIBS	 = -L../../bin -lzkachip-highlevel -lzkachip-lowlevel -ldl

SOURCES = t_hbci_cardinfo.cpp \
          t_hbci_cardtype.cpp \
          t_ddv_checkpin.cpp \
          t_ddv_editdata.cpp \
          t_ddv_readdata.cpp \
          t_rsa_cardinfo.cpp \
          t_rsa_editkeys.cpp \
	  t_test.cpp

PROGRAMS = ../../bin/t_hbci_cardinfo \
           ../../bin/t_hbci_cardtype \
           ../../bin/t_ddv_checkpin \
           ../../bin/t_ddv_editdata \
           ../../bin/t_ddv_readdata \
           ../../bin/t_rsa_cardinfo \
           ../../bin/t_rsa_editkeys \
	   ../../bin/t_test

.SUFFIXES: .cpp .o

../../bin/%.o: %.cpp
	$(CC) -c -o $@ $(CFLAGS) $<
../../bin/%: ../../bin/%.o ../../bin/libzkachip-highlevel.a ../../bin/libzkachip-lowlevel.a
	$(CC) -fPIC -o $@ $< $(LIBS)

all: depend $(PROGRAMS)

depend: Makefile.depend

Makefile.depend: $(SOURCES)
	for file in $(SOURCES); do echo -n "../../bin/"; gcc $(CFLAGS) -MM $$file; done >Makefile.depend

include Makefile.depend
