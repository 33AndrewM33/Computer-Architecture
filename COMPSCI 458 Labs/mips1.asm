# Loop n times, printing hello world
.data
prompt: .asciiz "How many iterations?: "
hello: .asciiz

.text
	# print a prompt, asking for how mnay iterations
	li $v0, 4
	la $a0, prompt
	syscall
	
	# read input
	
	#call print_gello_n to print hello world n times