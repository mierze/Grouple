<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['password']) || !isset($_POST['newPassword']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, password, newPassword)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$password= $_POST['password'];
	
	$newPassword= $_POST['newPassword'];
	
	#Validate password min/max length 
	if(strlen($newPassword) < 8 || strlen($newPassword) > 24)
	{
		$result["success"] = 0;
		$result["message"] = "Password must be between 8 and 24 characters.";
		echo json_encode ( $result );
		$mysqli->close();
		exit();
	}

	//first check if specified password is correct for specified user
	$stmt = $mysqli->prepare("SELECT password FROM users WHERE email = ?");
	$stmt->bind_param('s', $email);
	if($stmt->execute())
	{
		$mysqlresult = $stmt->get_result();
		$row = $mysqlresult->fetch_row();
		if(mysqli_num_rows($mysqlresult) < 1)
		{
			$result["success"] = 0;
			$result["message"] = "That email address is not registered!";
			echo json_encode ( $result );	
			exit();
		}
		else
		{
			//make sure hash password matches database hash
			if(password_verify($password, $row[0]))
			{
				//generate new password hash
				$password_hash = password_hash($newPassword, PASSWORD_DEFAULT);
				if($password_hash == false)
				{
					$result["success"] = 0;
					$result["message"] = "Internal server problem!  Please inform admin.";
					echo json_encode ( $result );
					exit();
				}
				//update password hash in table
				$stmt = $mysqli->prepare("UPDATE users SET password=? WHERE email = ?");
				$stmt->bind_param('ss', $password_hash, $email);
				if($stmt->execute())
				{
					$result["success"] = 1;
					$result["message"] = "Password updated successfully";
					echo json_encode ( $result );
				}
				else
				{
					$result["success"] = 0;
					$result["message"] = "Internal server problem! Please try again later.";
					echo json_encode ( $result );
				}
			}
			else
			{
				$result["success"] = 0;
				$result["message"] = "Incorrect password specified! Please try again.";
				echo json_encode ( $result );
			}
		}
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "Internal server problem! Please try again later.";
		echo json_encode ( $result );
	}
	$mysqli->close();
?>