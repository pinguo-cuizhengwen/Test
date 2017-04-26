#!/usr/bin/env python
import os
import sys
import argparse
import subprocess

parser = argparse.ArgumentParser(description='tagging samples')
parser.add_argument('--config', required=True)
args = parser.parse_args(sys.argv[1:])

if not os.path.exists('lib/'):
    print 'lib folder not found, please run this command in application top directories'
else:
    config_files = map(lambda f: str(f), filter(lambda x: str(x).endswith('.conf'), os.listdir('.')))
    jar_files = map(lambda x: "lib/%s" % x, os.listdir('lib/'))
    commands = [
        'spark-submit',
        '--class us.pinguo.bigdata.tagging.Bootstrap',
        '--master yarn',
        '--deploy-mode client',
        '--driver-memory 1g',
        '--executor-memory 1g',
        '--executor-cores 3',
        '--num-executors 12',
        '--files %s' % (','.join(config_files)),
        '--jars %s' % (','.join(jar_files)),
        'lib/photo-tagging.photo-tagging-0.1-SNAPSHOT.jar',
        '%s' % args.config
    ]
    subprocess.call(' '.join(commands), shell=True)
