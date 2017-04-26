#!/usr/bin/env python
import os
import sys
import argparse
import subprocess

parser = argparse.ArgumentParser(description='entity-streaming')
parser.add_argument('--pid', required=True)
args = parser.parse_args(sys.argv[1:])

if not os.path.exists('lib/'):
    print 'lib folder not found, please run this command in application top directories'
else:
    config_files = map(lambda f: str(f), filter(lambda x: str(x).endswith('.conf'), os.listdir('.')))
    product_config_files = map(lambda f: './config/%s/%s' % (args.pid, f), filter(lambda x: str(x).endswith('.conf'), os.listdir('./config/%s' % args.pid)))
    jar_files = map(lambda x: "lib/%s" % x, os.listdir('lib/'))
    commands = [
        'spark-submit',
        '--class us.pingguo.test.TestSubmit',
        '--master yarn',
        '--deploy-mode client',
        '--driver-memory 2g',
        '--executor-memory 4g',
        '--executor-cores 2',
        '--num-executors 4',
        '--files %s' % (','.join(config_files + product_config_files)),
        '--jars %s' % (','.join(jar_files)),
        'lib/us.pinguo.bigdata.test-1.0.jar',
        '%s-streaming.conf' % args.pid
    ]
    print ' '.join(commands)