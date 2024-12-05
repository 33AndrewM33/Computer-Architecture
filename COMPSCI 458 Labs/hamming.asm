.data
    dna_seq_1: .asciiz "CTTCAACGACCCGTACACGTGGCACTTCAGGAGGCGCCCGCAGGGGGGAA"
    dna_seq_2: .asciiz "CTTGAACGTCCCGTACACGTCCCACTTCAGGAGGCGCCAGTAGGGGAGAA"
    result: .asciiz "The sequences have a Hamming distance of "
    newline: .asciiz "\n"
    
.text
    
    # address for sequence 1 as first argument
    la $a0, dna_seq_1
    # address for sequence 2 as second argument
    la $a1, dna_seq_2
    
    # Call hamming proc
    jal CALC_HAMMING
    
    # Store result
    move $t0, $v0
    
    # Print result
    li $v0, 4
    la $a0, result
    syscall
    
    li $v0, 1
    move $a0, $t0
    syscall
    
    li $v0, 4
    la $a0, newline
    syscall
    
    # Cleanly exit
    li $v0, 10
    syscall
    
    ## Result should look like:
    ## The sequences have a Hamming distance of 7
    
    
## Hamming distance procedure
# Args:
#    - a0: address pointing to first sequence
#    - a1: address pointint to second sequence
# Returns:
#    - v0: hamming distance of first and second sequence
##
CALC_HAMMING:
    li $t0, 0      # $t0 will store the Hamming distance
    li $t1, 0      # $t1 is the loop counter (index)

compare_loop:
    lb $t2, 0($a0)      # Load the current character from dna_seq_1
    lb $t3, 0($a1)      # Load the current character from dna_seq_2

    # Check if we've reached the null terminator of either sequence
    beq $t2, $zero, end_calc   
    beq $t3, $zero, end_calc   

    # Compare characters, if different, increment hamming distance
    bne $t2, $t3, increment    
    j skip_increment           

increment:
    addi $t0, $t0, 1          

skip_increment:
    # Move to the next character
    addi $a0, $a0, 1           
    addi $a1, $a1, 1           
    j compare_loop             

end_calc:
    # Return the Hamming distance in v0
    move $v0, $t0
    jr $ra                    




