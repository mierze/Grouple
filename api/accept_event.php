<?php
	header('Content-type: application/json');
	include_once('../db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
	$response = array();
	//ensure all inputs have been sent
	if(!isset($_POST['email']) || !isset($_POST['id']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (email or id)";
		echo json_encode($response);
		exit();
	} 
	
	//stmt to make sure event is joinable
	$stmt = $mysqli->prepare("SELECT joinable FROM events WHERE e_id = ?");
	if($stmt === false)
	{
		$response["success"] = -1;
		$response["message"] = "Invalid SQL statement #1.";
		echo json_encode($response);
		exit();
	}
	$stmt->bind_param('s', $_POST['id']);
	if($stmt->execute())
	{
		$stmt->bind_result($joinable);
		$stmt->store_result();
		$stmt->fetch();
	}
	else
	{
		$response["success"] = -2;
		$response["message"] = "SQL execution fail.";
		echo json_encode($response);
		exit();
	}
	
	//check joinable's value (must be "Yes" in order to accept memeber.)
	if($joinable === 'Yes')
	{
		$stmt = $mysqli->prepare("UPDATE e_members SET rec_date = CURRENT_TIMESTAMP where email = ? AND e_id = ? AND rec_date is NULL");
		if($stmt === false)
		{
			$response["success"] = 0;
			$response["message"] = "Invalid SQL statement #2.";
			echo json_encode($response);
			exit();
		}
		$stmt->bind_param('ss', $_POST['email'], $_POST['id']);
		if($stmt->execute())
		{
			if($mysqli->affected_rows > 0)
			{
				$response["success"] = 1;
				$response["message"] = "Event invite accepted!";
				$response["confirmed"] = 0;
				
				//credit all groups that this member received e_id invites from
				$stmt = $mysqli->prepare("UPDATE e_groups SET rec_date = CURRENT_TIMESTAMP WHERE e_id = ? AND email = ?");
				if($stmt === false)
				{
					$response["success"] = -3;
					$response["message"] = "Invalid SQL statement.(#3)";
					echo json_encode($response);
					exit();
				}
				$stmt->bind_param('ss', $_POST['id'], $_POST['email']);
				if($stmt->execute())
				{}
			
				//Now that member has accepted the event invite and both tables have been updated, 
				//we must now check to see if eventstate should be updated as well (reached required minimum or maximum)
			
				//stmt to get min_part number of specified event
				$stmt = $mysqli->prepare("SELECT min_part, max_part FROM events WHERE e_id = ?");
				if($stmt === false)
				{
					$response["success"] = 0;
					$response["message"] = "Invalid SQL statement #4.";
					echo json_encode ( $response );
					exit();
				}
				$stmt->bind_param('s', $_POST['id']);
				if($stmt->execute())
				{
					$stmt->bind_result($minPart, $maxPart);
					$stmt->store_result();
					$stmt->fetch();
				
					//stmt to get count of accepted members
					$stmt = $mysqli->prepare("SELECT COUNT(*) FROM e_members WHERE e_id = ? AND rec_date IS NOT NULL");
					if($stmt === false)
					{
						$response["success"] = -4;
						$response["message"] = "Invalid SQL statement #5.";
						echo json_encode($response);
						exit();
					}
					$stmt->bind_param('s', $_POST['id']);
					if($stmt->execute())
					{
						$stmt->bind_result($count);
						$stmt->store_result();
						$stmt->fetch();

						//we can now compare min_part and count.
						if($count >= $minPart)
						{
							if($count === $maxPart)
							{
								//we've reached max_part! update Joinable to "No"
								$stmt = $mysqli->prepare("UPDATE events SET joinable = 'No', eventstate = 'Confirmed' WHERE e_id = ? AND (eventstate='Proposed' OR eventstate='Confirmed')");
								if($stmt === false)
								{
									$response["success"] = -5;
									$response["message"] = "Invalid SQL statement #6.";
									echo json_encode($response);
									exit();
								}
								$stmt->bind_param('s', $_POST['id']);
								if(!$stmt->execute())
								{
									$response["success"] = -6;
									$response["message"] = "SQL execution fail.";
									echo json_encode ( $response );
									exit();
								}
								else
								{
									if($mysqli->affected_rows > 0)
									{	
										$response["success"] = 1;
									}
								}
								
								//Since joinable is now known to be "No", we can clean up any remaining NULL invites still in the table for that e_id.
								$stmt = $mysqli->prepare("DELETE from e_members WHERE e_id = ? AND rec_date IS NULL");
								if($stmt === false)
								{
									$response["success"] = -7;
									$response["message"] = "Invalid SQL statement #7.";
									echo json_encode ( $response );
									exit();
								}
								$stmt->bind_param('s', $_POST['eid']);
								if(!$stmt->execute())
								{
								$response["success"] = -8;
								$response["message"] = "SQL execution fail.";
								echo json_encode($response);
								exit();
								}	
							}
							else
							{
								//we've reached min_part! update eventstate to "Confirmed"
								$stmt = $mysqli->prepare("UPDATE events SET eventstate = 'Confirmed' WHERE e_id = ? AND eventstate='Proposed'");
								if($stmt === false)
								{
									$response["success"] = 0;
									$response["message"] = "Invalid SQL statement #8.";
									echo json_encode($response);
									exit();
								}
								$stmt->bind_param('s', $_POST['id']);
								if(!$stmt->execute())
								{
									$response["success"] = 0;
									$response["message"] = "SQL execution fail.";
									echo json_encode($response);
									exit();
								}
								else
								{
									if($mysqli->affected_rows > 0)
									{	
										$response["success"] = 1;
									}
								}
							}		
						}
					}
					else
					{
						$response["success"] = -9;
						$response["message"] = "SQL execution fail.";
						echo json_encode($response);
						exit();
					}	
				}
				else
				{
					$response["success"] = -10;
					$response["message"] = "SQL execution fail.";
					echo json_encode($response);
					exit();
				}
				echo json_encode($response);
				exit();
			}
			else
			{
				$response["success"] = -11;
				$response["message"] = "Event invite was not found!";
				echo json_encode($response);
				exit();
			}
		}
		else
		{
			$response["success"] = -12;
			$response["message"] = "SQL execution fail.";
			echo json_encode($response);
			exit();
		}		
	}
	else
	{						
		$response["success"] = 0;
		$response["message"] = "Event no longer in join-able state!  (Either maximum capacity or event has already taken place)";
		echo json_encode($response);
		exit();
	}
	$mysqli->close();
?>