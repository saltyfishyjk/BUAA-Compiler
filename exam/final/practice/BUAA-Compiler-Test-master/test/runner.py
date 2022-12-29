import subprocess

class Runner:

    def __init__(self, testPath, ansPath, inputPath = None, mars = False):
        self.testPath = testPath # 测试文件路径
        self.ansPath = ansPath # 答案路径
        self.inputPath = inputPath # 输入文件路径
        self.mars = mars

    def copyFile(self, source, destination):
        sourceFile = open(source, "r", encoding="utf-8")
        contents = sourceFile.readlines()
        sourceFile.close()
        destFile = open(destination, "w")
        for s in contents:
            destFile.write(s)
        destFile.close()    


    def writeToTestFile(self):
        # 将输入写入testfile
        self.copyFile(source=self.testPath, destination="./testfile.txt")

    def writeToAnsFile(self):
        # 将答案写入ansfile
        self.copyFile(source=self.ansPath, destination="./ansfile.txt") 

    def writeToInputFile(self):
        if self.inputPath != None:
            self.copyFile(source=self.inputPath, destination="./input.txt")

    def check(self):
        # 比较ansfile和outputfile
        ansFile = open("./ansfile.txt","r")
        outputFile = open("./error.txt","r")
        ans = ansFile.readlines()
        outputs = outputFile.readlines()
        if len(ans) != len(outputs):
            return False
        for i in range(0,len(ans)):
            if ans[i].split() != outputs[i].split():
                return False
        return True                  

    def getStdInput(self):
        if self.inputPath == None:
            self.stdInput = ""
            return self.stdInput

        inputFile = open("./input.txt","r")
        inputs = inputFile.readlines()
        self.stdInput = ""
        for s in inputs:
            self.stdInput += s
        return self.stdInput    

    def runMars(self):
        cmd = ['java','-jar','mars.jar','mips.txt', 'nc']
        cwd = './'
        process = subprocess.Popen(args=cmd,
                                    cwd=cwd,
                                    stdout=subprocess.PIPE,
                                    stderr=subprocess.PIPE,
                                    stdin=subprocess.PIPE)

        stdout = process.communicate(input=str.encode(self.getStdInput()))
        f = open('output.txt','w')
        for b in stdout:
            f.write(bytes.decode(b))
        f.close()    
                                                      

    def run(self):
        ## read input
        self.writeToTestFile()
        self.writeToAnsFile()
        self.writeToInputFile()
        cmd = ['java','-jar','Compiler.jar']   
        cwd = "./"
        process = subprocess.run(args=cmd,
                                    cwd=cwd,
                                    stdout=subprocess.PIPE,
                                    #stderr=subprocess.PIPE,
                                    stdin=subprocess.PIPE)
        #process.communicate(input=str.encode(self.getStdInput()))
        if self.mars:
            self.runMars()
     







