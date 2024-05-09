import logging.config
import yaml

class BusinessFilter(logging.Filter):

    def __init__(self):
        super(BusinessFilter, self).__init__()

    def filter(self, record):
        isOk = ('Request ID:' in record.msg)
        if isOk:
           record.msg = record.msg
        return isOk