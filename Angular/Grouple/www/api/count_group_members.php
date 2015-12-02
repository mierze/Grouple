<?php
	include_once('../db_connect.inc.php');

	$gname = $_POST['gname'];

	if(isset($_POST['notify_box'])){ $notify = $_POST['notify_box']; }
					 				 
		$stmt = $mysqli->prepare("SELECT * FROM groupsnew WHERE g_name = ?");
		$stmt->bind_param('s', $gname);
		$stmt->execute();
		#$stmt->bind_result($response);
		$stmt->store_result();

		$row_cnt = $stmt->num_rows;

		if($stmt === false){
			 $result["success"] = 0;
			 $result["message"] = "Internal server problem. Please inform admin.";
			 echo json_encode ($result);
			 exit();
		}
		else{
			$result["success"] = 1;
			$result["gcount"] = $row_cnt;
			echo json_encode ($result);
		}
		$stmt->close();
		$mysqli->close();
?>