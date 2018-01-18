from pydub import AudioSegment
import socket

def play_music(filepath, client):
    # determine file type
    index = filepath.rfind(".")
    extension = filepath[index + 1:]
    # extension.replace("\n", "")
    base_name = filepath[:index]

    base_name = base_name[base_name.find("/") + 1:]

    # get audio data and convert it to wav
    sound = AudioSegment.from_file(filepath, format=extension)
    sound.export("to_render.wav", format="wav", parameters=["-ar", "4000", "-ac", "1", "-acodec", "pcm_u8"])
    sound = AudioSegment.from_wav("to_render.wav")
    sound_raw = sound.raw_data

    sound_raw_index = 0
    sound_raw_length = len(sound_raw)
    song_name_tx = b"<new>" + base_name.encode()
    while len(song_name_tx) < 37:
        song_name_tx += b"."
    client.send(song_name_tx)

    while sound_raw_index < sound_raw_length:
        client_received = client.recv(7)
        client.send(sound_raw[sound_raw_index : sound_raw_index + 32])
        sound_raw_index += 32

def main():
    should_exit = False
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    while should_exit == False:
        choice = input("Enter your action: ")
        choice_split = choice.split(" ")
        if choice_split[0] == "connect":
            client.connect((choice_split[1], int(choice_split[2])))
        elif choice_split[0] == "play":
            play_music(choice_split[1], client)
        elif choice_split[0] == "exit":
            should_exit = True

if __name__ == "__main__":
    main()
