class Event(object):
	def __init__(self, json):
		super(Event, self).__init__()
		print(type(json))
		print(json)
		self.event_id = json['event_id']
		self.owner_id = json['owner_id']
		self.attendees = []

	def get_event_id(self):
		return self.event_id

	def get_owner_id(self):
		return self.owner_id

	def get_attendees(self):
		return self.attendees