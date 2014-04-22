#
# Makefile:
#	libdht - DHT Java (JNI) library for the Raspberry Pi
#
TARGET=../../../target/libdht.so

#DEBUG	= -g -O0
DEBUG	= -O3
CC	= gcc
INCLUDE	= -I. -I/$(JAVA_HOME)/include -I/usr/local/include -I. -I/$(JAVA_HOME)/include/linux -I/usr/local/include/linux
CFLAGS	= $(DEBUG) -Wall $(INCLUDE) -Winline -pipe $(LIBS)

LIBS    = -L/usr/local/lib /usr/local/lib/libwiringPiDev.so /usr/local/lib/libbcm2835.a

# Should not alter anything below this line
###############################################################################

SRC	=   org_wso2_iot_refarch_rpi_agent_DHTSensor.c

OBJ	=   org_wso2_iot_refarch_rpi_agent_DHTSensor.o

all:		$(TARGET)

$(TARGET):	$(OBJ)
	@echo [LINK] $(OBJ)
	@$(CC) $(OBJ) -shared -o $(TARGET) $(INCLUDE) $(LIBS)
	@size   $(TARGET)

.c.o:
	@echo [COMPILE] "$(CC) -c $(CFLAGS)" $<
	@$(CC) -c $(CFLAGS) $< -o $@

clean:
	rm -f $(OBJ) $(TARGET) *~ core tags Makefile.bak

tags:	$(SRC)
	@echo [ctags]
	@ctags $(SRC)

depend:
	makedepend -Y $(SRC)

install:	$(TARGET)
	@echo [install]
	install -m 0755 -d /usr/local/lib
	install -m 0755 -d /usr/local/include
	install -m 0644 $(TARGET)	/usr/local/lib

uninstall:
	@echo [uninstall]
	rm -f /usr/local/lib/$(TARGET)


# DO NOT DELETE
org_wso2_iot_refarch_rpi_agent_DHTSensor.o: org_wso2_iot_refarch_rpi_agent_DHTSensor.h
