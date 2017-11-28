class Event(object):
    def __init__(self, json):
        super(Event, self).__init__()
        print(type(json))
        print(json)
        self.title = json['title']
        self.description = json['description']
        self.geo_loc = json['geo_loc']
        self.place_id = json['place_id']
        self.time = json['time']
        self.owner = json['owner']
        self.category = json['category']

    def get_title(self):
        return self.title

    def get_description(self):
        return self.description

    def get_geo_loc(self):
        return self.geo_loc

    def get_place_id(self):
        return self.place_id

    def get_time(self):
        return self.time

    def get_owner(self):
        return self.owner        

    def get_category(self):
        return self.category