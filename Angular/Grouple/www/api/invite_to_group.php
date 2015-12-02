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

    foreach ($invites as $email => $role)
    {
		$stmt = $mysqli->prepare("INSERT INTO g_members (email, g_id, sender, role) 
		VALUES (?,?,?,?)");
		if($stmt === false)
		{
			$response["success"] = -2;
			$response["message"] = "Invalid SQL statement.(#1)";
		}

		$stmt->bind_param('ssss', $email, $id, $from, $role);
	
    
        if($stmt->execute())
        {
            $response["success"] = 1;
            $response["message"] = "User has been invited to the group.";
           
		   /* 
        
                //Now that finished adding groupmate, initiate php (IN BACKGROUND) to handle email notification of this invite.  Because it runs in background, process will have minimal effects on JSON return response.
                // Note: potential improvement if used pclose(popen(command, "r")) instead.
                //$WshShell = new COM("WScript.Shell");
                //$oExec = $WshShell->Run("C:/wamp/bin/php/php5.5.12/php.exe -f C:/wamp/www/android_connect/mail.php G ".$sender.' '.$email.' '.$g_id, 0, false);
        
            */
        }
        else
        {
            $response["success"] = -3;
        }
        $response["count"]++;
    }
	echo json_encode($response);
	$mysqli->close();
    exit();
?>