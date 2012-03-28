#! /usr/bin/python

import os,sys,time
import re
from xml.etree.ElementTree import ElementTree


prog = 'java -jar simian-2.3.33.jar -formatter=xml "' + sys.argv[1] + '**/*.java"' +  ' | grep "^[^A-Z]" > out.xml'

print prog

start = time.time()
os.system(prog)
finish = time.time()

tree = ElementTree()
tree.parse("out.xml")


root = tree.getroot()
tree_iter = root.iter()

out = open( sys.argv[2], "w" )

i = 0
for element in tree_iter:
	if ( element.tag == 'block' ):
		out.write( element.get('sourceFile') + ' ' + element.get('startLineNumber') + ' ' + element.get('endLineNumber') )
		i += 1
		if ( i % 2 == 1 ):
			out.write( ' ' )
		else:
			out.write( '\n' ) 		

out.write(str(finish - start))
out.close()
os.remove("out.xml")




