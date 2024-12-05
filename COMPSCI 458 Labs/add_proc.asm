.data

.text

	li $a0, 10
	li $a1, 27
	li $a2, 3
	jal add_proc3
	
	move $a0, $v0
	li $v0, 1
	syscall
	
	li $v0, 10
	syscall

	########
	### ADD PROC
	### $a0 contains left operand
	### $a1 contains right operand
	### $A2 contains third operand
	### $v0 contains sum
	########


add_proc3:
	# prologue
	subi $sp, $sp, 4
	sw $ra, 0($sp)
	
	add $v0, $a0, $a1
	move $a0, $v0
	move $a1, $a2
	
	jal add_proc2
	
	lw $ra, 0($sp)
	addi $sp, $sp, 4
	
	jr $ra
	
add_proc2:
	add $v0, $a0, $a1
	jr $ra