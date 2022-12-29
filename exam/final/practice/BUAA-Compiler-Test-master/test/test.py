from runner import Runner
from recorder import Recorder

# CONFIG
TEST_TYPE = "error" #【修改】测试类型
TEST_ID_RANGE = [0, 14] #【修改】测试样例id范围
TESTCASE_DIR = "./data/" + TEST_TYPE + "_test/"
TEST_INPUT = False  # 【修改】是否提供输入
RUN_MARS = False  # 【修改】是否运行mars

recorder = Recorder(TEST_TYPE)

for id in range(TEST_ID_RANGE[0], TEST_ID_RANGE[1]+1):
    testPath = TESTCASE_DIR + TEST_TYPE + "_test_" + str(id) + ".txt"
    ansPath = TESTCASE_DIR + TEST_TYPE + "_ans_" + str(id) + ".txt"
    inputPath = None
    if TEST_INPUT:
        inputPath = TESTCASE_DIR + TEST_TYPE + "_input_" + str(id) + ".txt"
    runner = Runner(testPath=testPath, ansPath=ansPath, inputPath=inputPath, mars=RUN_MARS)
    runner.run()
    result = runner.check()
    recorder.addResult(id, result)
    if not result:
        break
    print("testing "+str(id) + '/' + str(TEST_ID_RANGE[1]),end='\r')

recorder.writeToLog()       
      
