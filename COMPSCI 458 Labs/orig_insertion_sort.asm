.data
	array: .space 180 # reserve 45 words worth of spcace
	len_array: .word 45
	newline: .asciiz "\n"
	sep: .asciiz ": "
	space: .asciiz " "
	even: .asciiz "Even"
	odd: .asciiz "Odd"

.text
	lw $t0 len_array
	move $t1, $zero
	la $t2, array
	
loop_random:
	beq $t0, $t1, loop_random_finished
	addi $t1, $t1, 1
	
	li $v0, 41
	move $a0, $zero
	syscall
	
	move $t3, $a0
	
	sw $t3, 0($t2)
	addi $t2, $t2, 4
	
	j loop_random
	
loop_random_finished:

	lw $t0, len_array # Outer max
	addi $t0, $t0, 1
	move $t1, $zero   # Outer counter
	la $t2, array	 # Outer pointer
	
loop_sort_outer:
	beq $t0, $t1, loop_sort_outer_finished
	addi $t1, $t1, 1
	
	sub $t3, $t0, $t1 # Inner max
	move $t4, $zero   # Inner counter
	move $t5, $t2     # Inner pointer
	move $t6, $t5     # Smallest address
	lw $t7, 0($t6)	 # Smallest value
	
	loop_sort_inner:
		beq $t3, $t4, loop_sort_inner_finished
		addi $t4, $t4, 1
		
		lw $t8, 0($t5)
		bgt $t8, $t7, loop_sort_inner_not_greater
		j loop_sort_inner_greater
		loop_sort_inner_not_greater:
			addi $t5, $t5, 4
			j loop_sort_inner
		loop_sort_inner_greater:
			move $t6, $t5
			move $t7, $t8
			addi $t5, $t5, 4
			j loop_sort_inner
	
		j loop_sort_inner
	
	loop_sort_inner_finished:
		lw $t9, 0($t2)
		sw $t7, 0($t2)
		sw $t9, 0($t6)
		addi $t2, $t2, 4
		j loop_sort_outer
	
loop_sort_outer_finished:
	lw $t0, len_array
	move $t1, $zero
	la $t2, array
	
loop_print:
	beq $t0, $t1, loop_print_finished
	
	li $v0, 1
	move $a0, $t1
	syscall
	
	li $v0, 4
	la $a0, sep
	syscall
	
	li $v0, 1
	lw $a0, 0($t2)
	syscall
	
	li $v0, 4
	la $a0, space
	syscall
	
	li $t3, 2
	lw $t4, 0($t2)
	div $t4, $t3
	mfhi $t5
	beq $t5, $zero, is_even
	j is_odd
is_even:
	li $v0, 4
	la $a0, even
	syscall
	j print_continue
is_odd:
	li $v0, 4
	la $a0, odd
	syscall
	j print_continue
	
print_continue:	
	
	li $v0, 4
	la $a0, newline
	syscall
	
	addi $t2, $t2, 4
	addi $t1, $t1, 1
	j loop_print

loop_print_finished:	

	li $v0, 10
	syscall
