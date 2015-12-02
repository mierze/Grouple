<?php
	header('Content-type: application/json');
	include_once('db_connect.inc.php');
	$_POST = json_decode(file_get_contents("php://input"), true);
    $response = array();
    //FOR TESTING
    $response["count"] = 0;
	//ensure all inputs have been sent
	if(!isset($_POST['invites']) || !isset($_POST['id']) || !isset($_POST['from']))
	{
		$response["success"] = -99;
		$response["message"] = "Missing required POST parameters (invites, id, from)";
		echo json_encode($response);
		exit();
	} 
	
	$invites = $_POST['invites'];
	$id = $_POST['id'];
    $from = $_POST['from'];

    //TODO: loop through invites and invite on eat a time
	//adding a creator (rec_date set automatically),
	//therefore, creator will automatically become a member of the group.
    foreach ($invites as $id)
    {
		//fetch list of users
			//for each one of those invite as role - M
			
	$getUsersStmt = $mysqli->prepare("SELECT e.e_id, e.e_name, e.min_part, e.max_part, e.start_date, em.sender FROM events e JOIN e_members em ON em.e_id = e.e_id where em.rec_date IS NULL and em.email = ?");
	$stmt->bind_param('s', $_POST['id']);
	$stmt->execute();
	$stmt->bind_result($id, $name, $min, $max, $start, $sender);
	$stmt->store_result();
	$row_cnt = $stmt->num_rows;
	if($row_cnt > 0)
	{
		$response["success"] = 1;
		$response["items"] = array();
		while($stmt->fetch())
		{
			$result = array();
			$result["id"] = $id;
			$result["name"] = $name;
			$result["minPart"] = $min;
			$result["maxPart"] = $max;
			$result["startDate"] = $start;
			$result["sender"] = $sender;
			$response["items"][] = $result;
		}	 
	}
	//TODO: don't return this stuff just use it to invite users
		/*
        if (strcmp($role, "C") == 0) 
        {
            $role = "A";
            $stmt = $mysqli->prepare("INSERT INTO e_members (email, sender, e_id, role, rec_date) 
            VALUES (?,'',?,?, CURRENT_TIMESTAMP)");
            if($stmt === false)
            {
                $response["success"] = -1;
                $response["message"] = "Invalid SQL statement.(#1)";
                echo json_encode($response);
                exit();
            }
            $stmt->bind_param('sss', $email, $id, $role);
        }
        //adding a non-creator (admin or regular member)
        else
        {			
            $stmt = $mysqli->prepare("INSERT INTO g_members (email, g_id, sender, role) 
            VALUES (?,?,?,?)");
            if($stmt === false)
            {
                $response["success"] = -2;
                $response["message"] = "Invalid SQL statement.(#1)";
            }
    
            $stmt->bind_param('ssss', $email, $id, $from, $role);
        }
    
        if($stmt->execute())
        {
            $response["success"] = 1;
            $response["message"] = "User has been invited to the group.";
            
            if(strcmp($role, "C") != 0)
            {
                //Now that finished adding groupmate, initiate php (IN BACKGROUND) to handle email notification of this invite.  Because it runs in background, process will have minimal effects on JSON return response.
                // Note: potential improvement if used pclose(popen(command, "r")) instead.
                //$WshShell = new COM("WScript.Shell");
                //$oExec = $WshShell->Run("C:/wamp/bin/php/php5.5.12/php.exe -f C:/wamp/www/android_connect/mail.php G ".$sender.' '.$email.' '.$g_id, 0, false);
            }	
        }
        else
        {
            $response["success"] = -3;
        }
        if (!$response["success"])
        {
           // $response["success"] = -1;
           // $response["message"] = "Failed to invite friend, please try again.";
        }
        */
        $response["count"]++;
    }
	$response['success'] = 1;
	echo json_encode($response);
	$mysqli->close();
    exit();
?>