import socket
import sys
s = socket.socket()
s.bind(("localhost",9999))
s.listen(10)

while True:
    sc, address = s.accept()

    print address
    i=1
    f = open("data.txt",'wb')
    i=i+1
    while (True):       
        l = sc.recv(1024)
        while (l):
                f.write(l)
                l = sc.recv(1024)
    f.close()
    sc.close()
s.close()