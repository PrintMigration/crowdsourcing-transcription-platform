// import React from 'react';
// import { Button, Divider } from 'semantic-ui-react';

// import banner from './assets/home-background.png';

// const divStyle = {
//   fontFamily: 'Abel',
//   backgroundColor: '#F9FAFB',
//   width: '550px',
//   height: '450px',
//   position: 'absolute',
//   top: '200px',
//   zIndex: 1,
//   padding: '6rem'
// };

// const imgStyle = {
//   maxWidth: '90%',
//   marginLeft: '10%',
//   position: 'absolute',
//   zIndex: 0
// };

// const overlapStyle = {
//   height: '600px',
//   position: 'relative'
// };

// class Home extends React.Component {
//   render() {
//     return (
//       <div className='test' style={{ maxWidth: '100%' }}>
//         <div style={overlapStyle}>
//           <div style={divStyle}>
//             <p
//               style={{
//                 textTransform: 'uppercase',
//                 fontSize: '13px',
//                 letterSpacing: '3px',
//                 color: 'grey'
//               }}
//             >
//               People, Religion, Information Networks, and Travel
//             </p>
//             <Divider />
//             <h1
//               style={{
//                 fontSize: '80px',
//                 fontWeight: 'bold',
//                 color: '#38bfa7',
//                 marginBottom: '0px'
//               }}
//             >
//               PRINT
//             </h1>
//             <p style={{ fontSize: '12px' }}>
//               A digital project that examines how the communication networks of
//               European religious minorities in the late seventeenth and early
//               eighteenth centuries shaped migration flows – especially to
//               British North America.
//             </p>
//             <div
//               style={{ position: 'absolute', bottom: '6rem', right: '6rem' }}
//             >
//               <Button compact floated='right' style={{ borderRadius: '0px' }}>
//                 Learn More
//               </Button>
//               <Button
//                 compact
//                 floated='right'
//                 style={{
//                   borderRadius: '0px',
//                   backgroundColor: 'transparent'
//                 }}
//               >
//                 Get Started
//               </Button>
//             </div>
//           </div>
//           <img
//             src={banner}
//             style={imgStyle}
//             alt='decorative header showing an example transcription document'
//           />
//         </div>
//       </div>
//     );
//   }
// }

// export default Home;

import React from "react";
import banner from "./assets/print-banner.png";
import { Button } from "semantic-ui-react";
import Axios from "axios";

// document.cookie="access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImQ1MmEwOTA1ODYifQ.eyJhdWQiOiIxMjE2NDFlZS05MWU4LTQwZWQtYmIwMi02N2ZkMDQyYzZlMDQiLCJleHAiOjE1ODU2NDc2NzIsImlhdCI6MTU4NTY0NDA3MiwiaXNzIjoiYWNtZS5jb20iLCJzdWIiOiJlNmRiNDQ3OS0zZDdjLTRjZDktYTE4Ni05MzRhYjQyOWU5OGUiLCJhdXRoZW50aWNhdGlvblR5cGUiOiJQQVNTV09SRCIsImVtYWlsIjoia2VsbHlka2ltQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJrZWxseWRraW0iLCJhcHBsaWNhdGlvbklkIjoiMTIxNjQxZWUtOTFlOC00MGVkLWJiMDItNjdmZDA0MmM2ZTA0Iiwicm9sZXMiOltdfQ.BBsvBBDtfmQmT_HOZLtbJBDlM_DoPf_zQzm8by5LLfk"

export default class Home extends React.Component {
  render() {
    return (
      <div>
        <img src={banner} style={{ width: "100%" }} />
      </div>
    );
  }
}
