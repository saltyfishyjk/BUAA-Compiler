import os

begin_str = "/*BEGIN*/"
end_str = "/*END*/"

c_file_name = "main.c"
sysy_file_name = "testfile1.txt"

if __name__ == "__main__":
	# c_file_name = input("Enter C file name : ")
	# sysy_file_name = input("Enter SysY file name (target file name) : ")
	if os.path.isfile(sysy_file_name):
		os.remove(sysy_file_name)
	start = False
	with open(c_file_name, 'r', encoding='utf-8') as c_file:
		lines = c_file.readlines()
		for line in lines:
			if not start:
				if begin_str in line:
					start = True
				continue
			if end_str in line:
				start = False
				continue
			with open(sysy_file_name, 'a', encoding='utf-8') as sysy_file:
				sysy_file.write(line)

