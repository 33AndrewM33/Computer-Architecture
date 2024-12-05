.data
    space_1: .space 40000
    space_2: .space 40000
    space_3: .space 40000
    word_1: .word 100

.text
    lw $t0, word_1
    li $t1, 0
    li $t2, 0

initialize_arrays:
    beq $t2, $t0, compute_sum
    li $t3, 0

initialize_columns:
    beq $t3, $t0, next_row_init

    mul $t5, $t2, $t0
    add $t5, $t5, $t3
    sll $t5, $t5, 2

    la $t4, space_1
    add $t4, $t4, $t5
    sw $t1, 0($t4)

    la $t4, space_2
    add $t4, $t4, $t5
    sw $t1, 0($t4)

    addi $t1, $t1, 1
    addi $t3, $t3, 1
    j initialize_columns

next_row_init:
    addi $t2, $t2, 1
    j initialize_arrays

compute_sum:
    li $t2, 0

compute_sum_rows:
    beq $t2, $t0, program_end
    li $t3, 0

compute_sum_columns:
    beq $t3, $t0, next_row_sum

    mul $t5, $t2, $t0
    add $t5, $t5, $t3
    sll $t5, $t5, 2

    la $t6, space_1
    add $t4, $t6, $t5
    lw $t6, 0($t4)

    la $t7, space_2
    add $t4, $t7, $t5
    lw $t7, 0($t4)

    add $t8, $t6, $t7
    la $t9, space_3
    add $t4, $t9, $t5
    sw $t8, 0($t4)

    addi $t3, $t3, 1
    j compute_sum_columns

next_row_sum:
    addi $t2, $t2, 1
    j compute_sum_rows

program_end:
    li $v0, 10
    syscall