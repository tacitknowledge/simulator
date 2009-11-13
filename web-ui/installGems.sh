#!/bin/sh
jruby -S gem list roodi | grep 'roodi'
if [ $? == 1 ];
then
	jruby -S gem install roodi --VERSION 2.0.1
fi
jruby -S gem list warbler | grep 'warbler'
if [ $? == 1 ];
then
	jruby -S gem install warbler --VERSION 0.9.14
fi
jruby -S gem list rails | grep 'rails'
if [ $? == 1 ];
then
	jruby -S gem install rails --VERSION 2.3.4
fi
jruby -S gem list relevance-rcov | grep 'relevance-rcov'
if [ $? == 1 ];
then
	jruby -S gem install relevance-rcov --source http://gems.github.com
fi
jruby -S gem list activerecord-jdbcmysql-adapter | grep 'activerecord-jdbcmysql-adapter'
if [ $? == 1 ];
then
	jruby -S gem install activerecord-jdbcmysql-adapter --VERSION 0.9.2
fi

jruby -S gem list activerecord-jdbc-adapter | grep 'activerecord-jdbc-adapter'
if [ $? == 1 ];
then
	jruby -S gem install activerecord-jdbc-adapter --VERSION 0.9.2
fi

