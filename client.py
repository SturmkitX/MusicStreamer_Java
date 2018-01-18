from pydub import AudioSegment
import socket
import threading

class MusicPlayer(threading.Thread):
    def __init__(self, client):
        threading.Thread.__init__(self)
        self.client = client
        self.should_exit = False
        self.sound_raw = None
        self.sound_raw_index = 0
        self.sound_raw_length = 0

    def run(self):
        while self.sound_raw_index < self.sound_raw_length:
            client_received = self.client.recv(7)
            self.client.send(self.sound_raw[self.sound_raw_index : self.sound_raw_index + 32])
            self.sound_raw_index += 32
            if self.should_exit == True:
                break

        self.client.close()

    def set_exit(self, flag):
        self.should_exit = flag

    def set_input_file(self, filepath):
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
        self.sound_raw = sound.raw_data

        self.sound_raw_index = 0
        self.sound_raw_length = len(self.sound_raw)
        song_name_tx = b"<new>" + base_name.encode()
        while len(song_name_tx) < 37:
            song_name_tx += b"."
        self.client.send(song_name_tx)


def main():
    should_exit = False
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    active_player = None

    while should_exit == False:
        choice = input("Enter your action: ")
        choice_split = choice.split(" ")
        if choice_split[0] == "connect":
            client.connect((choice_split[1], int(choice_split[2])))
        elif choice_split[0] == "play":
            if active_player is not None:
                active_player.set_input_file(choice_split[1])
            else:
                active_player = MusicPlayer(client)
                active_player.set_input_file(choice_split[1])
                active_player.start()
        elif choice_split[0] == "exit":
            if active_player is not None:
                active_player.set_exit(True)
            while active_player.is_alive():
                pass
            should_exit = True

if __name__ == "__main__":
    main()
