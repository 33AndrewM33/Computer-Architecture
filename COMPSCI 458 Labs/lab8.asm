.data
	space_1: .space 40000       
    	space_2: .space 40000       
    	space_3: .space 40000        
    	word_1: .word 100           


.text
	lw $t0, word_1            
    	li $t1, 0                
    	li $t2, 0                
    
init_space_1:
    	beq $t0, $t2, done_space_1  
    	li $t3, 0                   
    	
	row_loop_space_1:
		beq $t0, $t3, next_row_1    

   		la $t4, space_1         
    		mul $t5, $t3, $t0           
    		add $t5, $t5, $t2          
    		sll $t5, $t5, 2            
    		add $t4, $t4, $t5          
    		sw $t1, 0($t4)             
   		addi $t1, $t1, 1         
    		addi $t3, $t3, 1            
    		j row_loop_space_1         

		
	next_row_1:
    		addi $t2, $t2, 1            
    		j init_space_1        

done_space_1:
   	li $t1, 0                
    	li $t2, 0                 
	
init_space_2:
   	beq $t0, $t2, done_space_2  
    	li $t3, 0                   
	row_loop_space_2:
		beq $t0, $t3, next_row_2    

    		la $t4, space_2             
    		mul $t5, $t3, $t0           
    		add $t5, $t5, $t2           
    		sll $t5, $t5, 2             
    		add $t4, $t4, $t5           
    		sw $t1, 0($t4)              
    		addi $t1, $t1, 1            
    		addi $t3, $t3, 1            
    		j row_loop_space_2        

		
	next_row_2:
    		addi $t2, $t2, 1            
    		j init_space_2        
	
	
done_space_2:
    	li $t1, 0                  
    	li $t2, 0                  

compute_sum_space_3:
    	beq $t0, $t2, program_end   
    	li $t3, 0                   
	
	row_loop_space_3:
    		beq $t0, $t3, next_row_3    

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
    		j row_loop_space_3          
		
	next_row_3:
    		addi $t2, $t2, 1            
    		j compute_sum_space_3       

program_end:
    	li $v0, 10                  
    	syscall                     
