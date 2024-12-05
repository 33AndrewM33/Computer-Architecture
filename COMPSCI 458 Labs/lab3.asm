.data
prompt1: .asciiz "Please enter the first operand: "
prompt2: .asciiz "Please enter the second operand: "
prompt3: .asciiz "Please select the operation: \n1) Add\n2) Subtract\n3) Multiply\n4) Divide\n"


.text
	# print a prompt, asking for first operand
	li $v0, 4
	la $a0, prompt1
	syscall
	
	# read input
	li $v0, 5
	syscall
	move $t1, $v0
	
	# print a prompt, asking for second operand
	li $v0, 4
	la $a0, prompt2
	syscall
	
	# read input
	li $v0, 5
	syscall
	move $t2, $v0
	
	# print a prompt, asking for operation selection
	li $v0, 4
	la $a0, prompt3
	syscall
	
	# read input
	li $v0, 5
	syscall
	move $t3, $v0
	
	beq $t3, 1, add_proc
	beq $t3, 2, sub_proc
	beq $t3, 3, mult_proc
	beq $t3, 4, div_proc
	
end:
	li $v0 10
	syscall
	
add_proc:
	subi $sp, $sp, 4
	sw $ra, 0($sp)
	
	add $v0, $t1, $t2
	move $a0, $v0
	
	li $v0, 1
	syscall
	
	j end
sub_proc:
	subi $sp, $sp, 4
	sw $ra, 0($sp)
	
	sub $v0, $t1, $t2
	move $a0, $v0
	
	li $v0, 1
	syscall
	
	j end
mult_proc:
	subi $sp, $sp, 4
	sw $ra, 0($sp)
	
	mul $v0, $t1, $t2
	move $a0, $v0
	
	li $v0, 1
	syscall
	
	j end
div_proc:
	subi $sp, $sp, 4
	sw $ra, 0($sp)
	
	div $v0, $t1, $t2
	move $a0, $v0
	
	li $v0, 1
	syscall
	
	j end
