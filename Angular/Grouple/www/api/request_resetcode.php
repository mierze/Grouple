<?php
	//This php will attempt to generate a reset code for the specified user, and will send an email to them containing the code.

	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	@require_once '/../../bin/php/php5.5.12/pear/mail.php'; 
	include_once '/../../includes/stmt_connect.inc.php'; 

	//ensure all inputs have been sent
	if(isset($_POST['email']))
	{
		$email = $_POST['email'];
		$stmt = $mysqli->prepare("SELECT first,last FROM `users` WHERE email = ?");
		$stmt->bind_param('s', $email);
	}
	else
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email)";
		echo json_encode ( $result );
		exit();	
	}

	$stmt->execute();
	$stmt->store_result();
	$stmt->bind_result($first, $last);
	$row_cnt = $stmt->num_rows;
	
	if($row_cnt > 0)
	{	
		$stmt->fetch();
		
		//generate a reset code and store it in database
		$length = 6;
		$reset_code = substr(str_shuffle("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"), 0, $length);
		$stmt = $mysqli->prepare("UPDATE users SET reset_code=? WHERE email = ?");
		$stmt->bind_param('ss', $reset_code, $email);
		if(!$stmt->execute())
		{
			$result["success"] = 0;
			$result["message"] = "Unable to request a reset code.  Please try again later!";
			echo(json_encode($result));
			exit();
		}
		//send an email to that user with their reset code.
		$from = "Grouple <grouple-noreply@grouple.com>";		
		$to = $first.' '.$last.' <'.$email.'>';
		$subject = "Grouple Reset Password Request";
		$bodyline1 = 'Hi '.$first;
		$bodyline2 = ",\n\nHere is your Grouple reset password code:\n\n";
		$bodyline3 = $reset_code;
		$bodyline4 = "\n\nPlease type this code into your Grouple application's 'Forgot Password' window along with your email to create a new password.\n";
		$bodyline5 = "\nAs always, thanks for using Grouple!\n\n-Grouple Developers\n\n";
		
		$random_length = substr(str_shuffle("123456789"), 0, 1);
		
		$bodyline6 = substr(str_shuffle("-="), 0, $random_length);
	
		$body = $bodyline1.$bodyline2.$bodyline3.$bodyline4.$bodyline5.$bodyline6;
		$headers = array ('From' => $from,   'To' => $to,   'Subject' => $subject);
		$smtp = @Mail::factory('smtp',  
		array ('host' => $host,  
		'port' => $port,  
		'auth' => true,   
		'username' => $username,  
		'password' => $password));
		$mail = @$smtp->send($to, $headers, $body);
		if (@PEAR::isError($mail)) 
		{   
			$result["success"] = 0;
			$result["message"] = "Unable to request a reset code.  Please try again later!";		
		} 
		else 
		{ 
			$result["success"] = 1;
			$result["message"] = "Sucessfully sent a reset code to your email!";	
		} 
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Please verify your email address is correct!";		
	}	
	
	$stmt->close();
	echo(json_encode($result));
	$mysqli->close();
?>
