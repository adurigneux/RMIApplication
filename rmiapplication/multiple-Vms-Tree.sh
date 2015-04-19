#!/bin/bash

echo "---------Starting Script Tree---------"


for i in `seq 1 6`
	do xterm -e java -jar site-node.jar ${i} &
	pids[$i]=$!
done

sleep 2

#Tree client
xterm -e java -jar client-tree.jar &
pids[7]=$!

echo -n "Press enter to quit..."
read var_ok

for i in `seq 1 7`
	do kill -9 ${pids[$i]}
done 


echo "---------Ending Script---------"
