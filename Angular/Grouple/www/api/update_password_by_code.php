<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent (resetting password from loginActivity using email reset_code)
	if(!isset($_POST['email']) || !isset($_POST['code']) || !isset($_POST['newPassword']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email,code,newPassword)";
		echo json_encode ( $result );
		exit();
	}

	$code= $_POST['code'];
	$email= $_POST['email'];
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

	//first check if specified code is correct for specified user
	$stmt = $mysqli->prepare("SELECT reset_code FROM users WHERE email = ?");
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
			//make sure user supplied code matches database code
			if(strcmp($code, $row[0]) == 0)
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
				//update password hash in table, NULL out reset_code
				$stmt = $mysqli->prepare("UPDATE users SET password=?, reset_code=NULL WHERE email = ?");
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
				$result["message"] = "Your reset code may have expired.  Please try requesting a new code!";
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