# Loop n times, printing hello world
.data
prompt: .asciiz "How many iterations?: "
hello: .asciiz "Hello World!"

.text
	# print a prompt, asking for how mnay iterations
	li $v0, 4
	la $a0, prompt
	syscall
	
	# read input
	li $v0, 5
	syscall
	move $t0, $v0
	
	# call print_gello_n to print hello world n times
	jal HELLO_ITER_PROC
	
	# cleanly exit
	li $v0, 10	# <-- where $ra points to
	syscall
	
HELLO_ITER_PROC:
	# prologue
	subi $sp, $sp, 4
	sw $ra, 0($sp)
	
	li $t0, 0
	move $t1, $a0
	
loop:	
	beq $t0, $t1, end_loop
	addi $t0, $t0, 1
	li $v0, 4
	la $a0, hello
	syscall
	j loop
	
end_loop:
	
	# epilogue
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	
	jr $ra