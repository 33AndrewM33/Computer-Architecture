.data
prompt1: .asciiz "Enter the number of sequence numbers to print: \n"
out_format: .asciiz ", "

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
	
	# end program
	li $v0, 10
	syscall
	
loop:	
	beq $t0, $a0, end_loop
	addi $t0, $t0, 1
	jal rec_fib
	j loop
	
end_loop:
	
	# epilogue
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	
	jr $ra
	
rec_fib:
	beqz $a0, zero		# if n=0 return 0
	beq $a0, 1, one		# if n=1 return 1
	
	# fib(n-1)
	sub $sp, $sp, 4
	sw $ra, 0($sp)
	
	sub $a0, $a0, 1 # n-1
	jal rec_fib	# calling fib(n-1)
	add $a0, $a0, 1
	
	lw $ra, 0($sp)
	add $sp, $sp, 4
	
	sub $sp, $sp, 4	# push return value to stack
	sw $v0, 0($sp)
	
	sub $sp, $sp, 4
	sw $ra, 0($sp)
	
	# fib(n-2)
	sub $a0, $a0, 2	# n-2
	jal rec_fib	# calling fib(n-2)
	add $a0, $a0, 2
	
	# restore return address from stack
	lw $ra, 0($sp)
	add $sp, $sp, 4
	
	# pop return value from stack
	lw $s7, 0($sp)
	add $sp, $sp, 4
	
	# fib(n-2) + fib(n-1)
	add $v0, $v0, $s7
	jr $ra

zero:
	li $v0, 0
	jr $ra
one:
	li $v0, 1
	jr $ra
