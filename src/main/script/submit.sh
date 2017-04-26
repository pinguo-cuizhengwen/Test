#!/usr/bin/env bash
path=.
pid=camera360
if [ -n "$1" ]; then
    path=$1
fi
if [ -n "$2" ]; then
    pid=$2
fi
command=`python get_command.py --pid $pid`
echo $command
$command