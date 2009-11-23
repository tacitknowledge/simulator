# WARNING: cfengine distributes this with each vmupdate!  Local changes will be lost.
# Make changes in svn:dev-cf/projects/simulator.  
# See also: https://milestone.tacitknowledge.com/display/TKMain/Dev+Cfengine+Implementation
#

#!/bin/bash
#

/usr/local/mysql/bin/mysql -D simulator_dev --execute='SHOW TABLES' > /dev/null 2>&1

if [[ $? != 0 ]] ; then
/bin/echo "Script will create required databases"
cat <<EOF | /usr/local/mysql/bin/mysql

CREATE DATABASE simulator_dev;
CREATE DATABASE simulator_test;
CREATE DATABASE simulator_prod;

GRANT ALL ON simulator_dev.* TO simulator@'localhost' IDENTIFIED by 'password';
GRANT ALL ON simulator_test.* TO simulator@'localhost' IDENTIFIED by 'password';
GRANT ALL ON simulator_prod.* TO simulator@'localhost' IDENTIFIED by 'password';

FLUSH PRIVILEGES;
EOF
/bin/echo "Script ended creating required databases"
fi
