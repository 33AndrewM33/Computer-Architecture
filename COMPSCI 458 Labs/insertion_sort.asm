.data
	array: .space 180      # reserve 45 words worth of space
	len_array: .word 45
	newline: .asciiz "\n"
	sep: .asciiz ": "
	space: .asciiz " "
	even: .asciiz "Even"
	odd: .asciiz "Odd"

.text
	lw $t0, len_array
	move $t1, $zero         
	la $t2, array          
	
loop_random:
	beq $t1, $t0, loop_random_finished
	addi $t1, $t1, 1
	
	li $v0, 41               
	move $a0, $zero
	syscall
	
	sw $a0, 0($t2)           
	addi $t2, $t2, 4         
	
	j loop_random            

loop_random_finished:
	
	lw $t0, len_array        # Outer max
	move $t1, $zero          # Outer counter
	la $t2, array            # Outer pointer
	
loop_sort_outer:
	beq $t1, $t0, loop_sort_outer_finished
	addi $t1, $t1, 1
	
	move $t5, $t2            # Inner pointer
	move $t6, $t5            # Smallest value address
	lw $t7, 0($t6)           # Smallest value
	
	
	move $t4, $t1            # Inner counter (remaining elements)
	
	loop_sort_inner:
		beq $t4, $t0, loop_sort_inner_finished
		addi $t4, $t4, 1
	
		lw $t8, 4($t5)           # Load next value in array
		addi $t5, $t5, 4         # Move to next element
	
		blt $t8, $t7, update_smallest
		j loop_sort_inner

	update_smallest:
		move $t6, $t5            # Update smallest element address
		move $t7, $t8            # Update smallest value
		j loop_sort_inner

	loop_sort_inner_finished:
		lw $t9, 0($t2)
		sw $t7, 0($t2)
		sw $t9, 0($t6)
		addi $t2, $t2, 4         # Move to next position in array
		j loop_sort_outer
	
loop_sort_outer_finished:

	# Print array with even/odd check
	lw $t0, len_array
	move $t1, $zero
	la $t2, array
	
loop_print:
	beq $t1, $t0, loop_print_finished
	
	li $v0, 1
	move $a0, $t1
	syscall                  # Print index
	
	li $v0, 4
	la $a0, sep
	syscall                  # Print separator ": "
	
	li $v0, 1
	lw $a0, 0($t2)
	syscall                  # Print value
	
	li $v0, 4
	la $a0, space
	syscall                  # Print space
	
	# Check if value is even or odd
	lw $t4, 0($t2)
	andi $t5, $t4, 1         # Check LSB for even/odd
	bne $t5, $zero, is_odd   # If LSB is 1, it's odd
	
	# Even case
	li $v0, 4
	la $a0, even
	syscall
	j print_continue

is_odd:
	# Odd case
	li $v0, 4
	la $a0, odd
	syscall

print_continue:
	li $v0, 4
	la $a0, newline
	syscall                  # Print newline
	
	addi $t2, $t2, 4         # Move to next array element
	addi $t1, $t1, 1         # Increment counter
	j loop_print

loop_print_finished:
	li $v0, 10               
	syscall




