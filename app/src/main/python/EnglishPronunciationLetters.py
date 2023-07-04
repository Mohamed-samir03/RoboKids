def recognize_from_en_audio(path):
    import socket

    # set up a TCP/IP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # set up the server address and port
    server_address = ('192.168.1.2', 9999)

    # read the audio file
    with open(path, 'rb') as f:
        audio_data = f.read()

    # connect to the server
    sock.connect(server_address)

    # send the audio data to the server
    sock.sendall(audio_data)

    # receive the response from the server
    response = sock.recv(1024).decode('utf-8')

    # close the connection
    sock.close()

    return response

