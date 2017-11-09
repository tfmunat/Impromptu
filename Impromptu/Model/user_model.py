class User(object):
	def __init__(self, json):
		super(User, self).__init__()
		print(type(json))
		print(json)
		self.first_name = json['first_name']
		self.last_name = json['last_name']
		self.likes = json['likes']
		self.fb_id = json['fb_id']
		self.is_active = True

	def get_first_name(self):
		return self.first_name

	def get_last_name(self):
		return self.last_name

	def get_likes(self):
		return self.likes

	def get_fb_id(self):
		return self.fb_id

	def get_is_active(self):
		return self.is_active		