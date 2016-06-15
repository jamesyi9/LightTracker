from bottle import route, run, template, static_file
 
@route('/hello/<name>')
def index(name):
	return template('<b>Hello {{name}}</b>!', name=name)
 
@route('/<filename>')
def server_static(filename):
	return static_file(filename, root = '/home/pi/EECS113/finalproj/LightTracker/data')
 
run(host='localhost', port=8080)
