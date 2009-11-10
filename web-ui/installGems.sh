#!/bin/sh
roodi -h >> /dev/null
if [ $? == 127 ];
then
	jruby -S gem install roodi --VERSION 2.0.1
fi
warble -h >> /dev/null
if [ $? == 127 ];
then
	jruby -S gem install warbler --VERSION 0.9.14
fi
rails -h >> /dev/null
if [ $? == 127 ];
then
	jruby -S gem install rails --VERSION 2.4.3
fi
rcov -h >> /dev/null
if [ $? == 127 ];
then
	jruby -S gem install relevance-rcov --source http://gems.github.com
fi