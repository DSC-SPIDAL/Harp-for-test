from harp.ml.MLKernels import MLKernels


class HarpSession:

    def __init__(self, java_session):
        self.__java_session = java_session
        self.ml = MLKernels(java_session.ml())

    @property
    def name(self):
        return self.__java_session.getName()

    def byteTest(self, b):
        self.__java_session.byteTest(b)

    def calc(self, b, data):
        self.__java_session.calc(b, data)
