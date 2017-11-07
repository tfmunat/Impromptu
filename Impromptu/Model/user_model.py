class User(object):
	def __init__(self, json):
		super(User, self).__init__()
		print(type(json))
		print(json)
		self.first_name = json['first_name']
		self.last_name = json['last_name']
		self.likes = json['likes']
		self.email = json['email']
		self.is_active = True

	def get_first_name(self):
		return self.first_name

	def get_last_name(self):
		return self.last_name

	def get_likes(self):
		return self.likes

	def get_email(self):
		return self.email

	def get_is_active(self):
		return self.is_active		