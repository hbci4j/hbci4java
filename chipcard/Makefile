
all: lib/libhbci4java-card-linux.so

lib/libhbci4java-card-linux.so: bin/libhbci4java-card-linux.so
	cp bin/libhbci4java-card-linux.so lib/

bin/libhbci4java-card-linux.so: .make

.make:
	make -C src all

clean:
	-rm -f lib/libhbci4java-card-linux.so
	-rm -f bin/*
	-rm -f `find -name Makefile.depend`
