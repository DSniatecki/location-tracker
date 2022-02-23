import multiprocessing
import uuid
import requests

receiver_endpoint = 'http://localhost:10020/api/object-locations'
number_of_objects = 20
object1Id = 'c039e9fc-8b46-11ec-a8a3-0242ac120002'


def start_sending_locations(object_id):
    object_location = {
        'objectId': object_id,
        'latitude': '24.12124212',
        'longitude': '64.42127643'
    }
    while True:
        try:
            requests.post(receiver_endpoint, json=object_location)
        except _:
            pass


if __name__ == '__main__':
    print("Generating object ids ...")
    object_ids = [object1Id]
    for _ in range(number_of_objects - 1):
        object_ids.append(str(uuid.uuid4()))

    print("Object ids:")
    for i, object_id in enumerate(object_ids):
        print(f"{i + 1}.) {object_id}")

    processes = [multiprocessing.Process(target=start_sending_locations, args=(object_id,)) for object_id in object_ids]

    print("Sending object locations...")
    for process in processes:
        process.start()
    for process in processes:
        process.join()
