.PHONY: all clean

all:
	$(MAKE) -C source all
	$(MAKE) -C documents all
	$(MAKE) -C examples all

clean:
	$(MAKE) -C source clean
	$(MAKE) -C documents clean
	$(MAKE) -C examples clean

