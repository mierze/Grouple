<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');

	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['remove']) || !isset($_POST['e_id']))
	{
		$result["success"] = -99;
		$result["message"] = "Missing required POST parameters (email, remove, e_id)";
		echo json_encode ( $result );
		exit();
	} 
	
	$email= $_POST['email'];
	$remove = $_POST['remove'];
	$e_id = $_POST['e_id'];

	//updating a member
	if (strcmp($remove, "no") == 0) 
	{
		if(isset($_POST['hidden']))
		{
			if(isset($_POST['role']))
			{
				$role = $_POST['role'];
				$stmt = $mysqli->prepare("UPDATE e_members set role = ?, hidden = ? where email = ? and e_id = ?");
				if($stmt === false)
				{
					$result["success"] = 0;
					$result["message"] = "Invalid SQL statement.(#1)";
					echo json_encode ( $result );
					exit();
				}
				$stmt->bind_param('sisi', $role, $_POST['hidden'], $email, $e_id);
			}
			else
			{
				$stmt = $mysqli->prepare("UPDATE e_members set hidden = ? where email = ? and e_id = ?");
				if($stmt === false)
				{
					$result["success"] = 0;
					$result["message"] = "Invalid SQL statement.(#1)";
					echo json_encode ( $result );
					exit();
				}
				$stmt->bind_param('isi', $_POST['hidden'], $email, $e_id);
			}
		}
		else
		{
			$stmt = $mysqli->prepare("UPDATE e_members set role = ? where email = ? and e_id = ?");
			if($stmt === false)
			{
				$result["success"] = 0;
				$result["message"] = "Invalid SQL statement.(#2)";
				echo json_encode ( $result );
				exit();
			}
			$stmt->bind_param('ssi', $role, $email, $e_id);
		}
		
		
	}
	//removing a user
	else if(strcmp($remove, "yes") == 0) 
	{			
		$stmt = $mysqli->prepare("DELETE from e_members where email = ? and e_id = ?");
		if($stmt === false)
		{
			$result["success"] = 0;
			$result["message"] = "Invalid SQL statement.(#3)";
			echo json_encode ( $result );
			exit();
		}

		$stmt->bind_param('si', $email, $e_id);
	}
	else
	{
		$result["success"] = 0;
		$result["message"] = "invalid POST parameters (remove) must be set to \"no\" or \"yes\"";
		echo json_encode ( $result );
		exit();
	}

	if($stmt->execute())
	{
	    $result["success"] = 1;
   	    $result["message"] = "Updated event participant successfully.";
   	    echo json_encode ( $result );
	}
	else
	{
	    $result["success"] = 0;
	    $result["message"] = "Failed to update event participant.";
	    echo json_encode ( $result );
	}		
	
	$mysqli->close();
?>