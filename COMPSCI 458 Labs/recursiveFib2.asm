.data
prompt1: .asciiz "Enter the number of sequence numbers to print: \n"

.text
	# print a prompt, asking for the number of sequence numbers
	la $a0, prompt1
	li $v0, 4
	syscall
	
	# read input
	li $v0, 5
	syscall
	move $t2, $v0	# n stored in $t2
	
	# call rec_fib
	move $a0, $t2
	move $v0, $t2
	jal rec_fib
	move $t3, $v0
	
	# output
	move $a0, $t3
	li $v0, 1
	syscall
	
	
end:
	# end program
	li $v0, 10
	syscall
	
rec_fib:
	beq $a0, 1, end
	beq $a0, 0, end
	
	sub $a0, $a0, 1
	jal rec_fib
	add $a0, $a0, 1
	
	sub $a0, $a0, 2
	jal rec_fib
	add $a0, $a0, 2
	
	
	
	