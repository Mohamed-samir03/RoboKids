def convertImageToText(url):
    import socket
    # create a socket object
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # get local machine name
    host = '192.168.1.10'
    port = 9999

    # connection to hostname on the port.
    s.connect((host, port))

    # send a message to the server
    message = url
    s.send(message.encode('utf-8'))

    # receive data from the server
    data = s.recv(1024).decode('utf-8')

    # close the connection
    s.close()

    return data