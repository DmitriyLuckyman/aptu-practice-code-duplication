#! /usr/bin/python

import os
import re

f = open ( "folders.txt", 'r' )
rootDirectories = f.readlines()
f.close()

dList = []
for st in rootDirectories:
	strNoEsc = st.rstrip()
	if (strNoEsc != ''):
		dList.append( strNoEsc )

prog = "clonedigger"
for st in dList:
	prog += " " + st

#os.system(prog)



def get_file_pair(st):
	regexp = re.compile( '".[^"]*"' )
	files = []
	for s in regexp.findall(st):
		newString = ''.join(s.split('"'))
		files.append(newString)

	regexp = re.compile( 'The first line is [0-9]*' )
	position = []
	for s in regexp.findall(st):
		position.append(int(s.split(' ')[4]))

	regexp = re.compile( 'size = [0-9]*')
	codeLength = int(regexp.findall(st)[0].split(' ')[2])	
	l = []
	for i in range(2): 
		l.append( (files[i], position[i], position[i] + codeLength) )
	return l 






regexp = re.compile( ".*Clone #.*" )

f = open ( "output.html", "r" )
out = open ( "output.txt", "w" )


matched = []
while True:
	line = f.readline()
	if not line: break
	if re.search(regexp, line):
		files = get_file_pair(line)
		for i in range(2):
			out.write(files[i][0] + ' ' + str(files[i][1]) + ' ' + str(files[i][2]) + ' ')
		out.write('\n')		
	
f.close()

