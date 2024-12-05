.data
	space_1: .space 40000
	space_2: .space 40000
	space_3: .space 40000
	word_1: .word 100

.text
	lw $t0, word_1
	li $t1, 0
	li $t2, 0
	
label_1:
	beq $t0, $t2, label_4
	li $t3, 0
	label_2:
		beq $t0, $t3, label_3
			
		la $t4, space_1
		mul $t5, $t3, $t0
		add $t5, $t5, $t2
		sll $t5, $t5, 2
		add $t4, $t4, $t5	
		sw $t1, 0($t4)
		addi $t1, $t1, 1
		addi $t3, $t3, 1
		j label_2
		
	label_3:
		addi $t2, $t2, 1
		j label_1

label_4:
	li $t1, 0
	li $t2, 0
	
label_5:
	beq $t0, $t2, label_8
	li $t3, 0
	label_6:
		beq $t0, $t3, label_7
		
		la $t4, space_2
		mul $t5, $t3, $t0
		add $t5, $t5, $t2
		sll $t5, $t5, 2
		add $t4, $t4, $t5	
		sw $t1, 0($t4)
		addi $t1, $t1, 1
		addi $t3, $t3, 1
		j label_6
		
	label_7:
		addi $t2, $t2, 1
		j label_5	
	
	
label_8:
	li $t1, 0
	li $t2, 0

label_9:
	beq $t0, $t2, label_12
	li $t3, 0
	label_10:
		beq $t0, $t3, label_11
		
		
		mul $t5, $t3, $t0
		add $t5, $t5, $t2
		sll $t5, $t5, 2
		
		la $t6, space_1
		la $t7, space_2
		la $t8, space_3
		
		add $t4, $t6, $t5	
		lw $t6, 0($t4)
		
		add $t4, $t7, $t5
		lw $t7, 0($t4)
		
		add $t4, $t8, $t5
		add $t8, $t6, $t7
		sw $t8, 0($t4)
		
		addi $t1, $t1, 1
		addi $t3, $t3, 1
		j label_10
		
	label_11:
		addi $t2, $t2, 1
		j label_9

label_12:
	li $v0, 10
	syscall
